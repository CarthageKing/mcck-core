/*
 *  Copyright 2019 Qameta Software OÜ
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.carthageking.mc.mcck.core.allure.cucumber7.jvm;

/*-
 * #%L
 * mcck-core-allure-cucumber7-jvm
 * %%
 * Copyright (C) 2024 Michael I. Calderero
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static io.qameta.allure.util.ResultsUtils.createFrameworkLabel;
import static io.qameta.allure.util.ResultsUtils.createHostLabel;
import static io.qameta.allure.util.ResultsUtils.createLabel;
import static io.qameta.allure.util.ResultsUtils.createLanguageLabel;
import static io.qameta.allure.util.ResultsUtils.createTestClassLabel;
import static io.qameta.allure.util.ResultsUtils.createThreadLabel;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cucumber.messages.types.Feature;
import io.cucumber.plugin.event.TestCase;
import io.qameta.allure.cucumber7jvm.McckAllureCucumber7JvmTagParser;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.Link;
import io.qameta.allure.util.ResultsUtils;

// copied from LabelBuilder
class McckAllureCucumber7JvmLabelBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(McckAllureCucumber7JvmLabelBuilder.class);
	private static final String COMPOSITE_TAG_DELIMITER = "=";

	private static final String SEVERITY = "@SEVERITY";
	private static final String ISSUE_LINK = "@ISSUE";
	private static final String TMS_LINK = "@TMSLINK";
	private static final String PLAIN_LINK = "@LINK";
	private static final String OWNER = "@OWNER";

	// https://allurereport.org/docs/cucumberjvm-reference/#behavior-based-hierarchy
	private static final String LABEL_EPIC = "@allure.label.epic";
	private static final String LABEL_FEATURE = "@allure.label.feature";
	private static final String LABEL_STORY = "@allure.label.story";

	// https://allurereport.org/docs/cucumberjvm-reference/#suite-based-hierarchy
	//
	// this one also mentions alternate label names for parent_suite and sub_suite. Handle them as well
	// https://github.com/allure-framework/allure2/blob/2.27.0/allure-generator/src/test/java/io/qameta/allure/tags/TagsPluginTest.java#L313
	private static final String[] LABELS_PARENT_SUITE = { "@allure.label.parent_suite", "@allure.label.parentSuite" };
	private static final String[] LABELS_SUB_SUITE = { "@allure.label.sub_suite", "@allure.label.subSuite" };
	private static final String LABEL_SUITE = "@allure.label.suite";

	private final List<Label> scenarioLabels = new ArrayList<>();
	private final List<Link> scenarioLinks = new ArrayList<>();

	McckAllureCucumber7JvmLabelBuilder(final Feature feature, final TestCase scenario, final Deque<String> tags) {
		final McckAllureCucumber7JvmTagParser tagParser = new McckAllureCucumber7JvmTagParser(feature, scenario);

		while (tags.peek() != null) {
			final String tag = tags.remove();

			if (tag.contains(COMPOSITE_TAG_DELIMITER)) {

				final String[] tagParts = tag.split(COMPOSITE_TAG_DELIMITER, 2);
				if (tagParts.length < 2 || Objects.isNull(tagParts[1]) || tagParts[1].isEmpty()) {
					// skip empty tags, e.g. '@tmsLink=', to avoid formatter errors
					continue;
				}

				final String tagKey = tagParts[0].toUpperCase();
				final String tagValue = tagParts[1];

				// Handle composite named links
				if (tagKey.startsWith(PLAIN_LINK + ".")) {
					tryHandleNamedLink(tag);
					continue;
				}

				switch (tagKey) {
				case SEVERITY:
					getScenarioLabels().add(ResultsUtils.createSeverityLabel(tagValue.toLowerCase()));
					break;
				case TMS_LINK:
					getScenarioLinks().add(ResultsUtils.createTmsLink(tagValue));
					break;
				case ISSUE_LINK:
					getScenarioLinks().add(ResultsUtils.createIssueLink(tagValue));
					break;
				case PLAIN_LINK:
					getScenarioLinks().add(ResultsUtils.createLink(null, tagValue, tagValue, null));
					break;
				case OWNER:
					getScenarioLabels().add(ResultsUtils.createOwnerLabel(tagValue));
					break;
				default:
					LOGGER.warn("Composite tag {} is not supported. adding it as RAW", tagKey);
					getScenarioLabels().add(getTagLabel(tag));
					break;
				}
			} else if (tagParser.isPureSeverityTag(tag)) {
				getScenarioLabels().add(ResultsUtils.createSeverityLabel(tag.substring(1)));
			} else if (!tagParser.isResultTag(tag)) {
				getScenarioLabels().add(getTagLabel(tag));
			}
		}

		final String featureName = feature.getName();
		final URI uri = scenario.getUri();

		getScenarioLabels().addAll(ResultsUtils.getProvidedLabels());
		getScenarioLabels().addAll(Arrays.asList(
			createHostLabel(),
			createThreadLabel(),
			//createFeatureLabel(featureName),
			//createStoryLabel(scenario.getName()),
			//createSuiteLabel(featureName),
			createTestClassLabel(scenario.getName()),
			// FIXED: fixed the label
			createFrameworkLabel("mcck-core-cucumber7jvm"),
			createLanguageLabel("java"),
			createLabel("gherkin_uri", uri.toString())));

		// FIXED: get epic name from tag. If not exist, don't add it
		String epicName = retrieveEpicLabel(scenario.getTags());
		if (!epicName.isEmpty()) {
			getScenarioLabels().add(ResultsUtils.createEpicLabel(epicName));
		}

		// FIXED: get feature from tag. If not exist, don't add it
		String allureFeatureName = retrieveAllureFeatureName(scenario.getTags());
		if (!allureFeatureName.isEmpty()) {
			getScenarioLabels().add(ResultsUtils.createFeatureLabel(allureFeatureName));
		}

		// FIXED: get story from tag. If not exist, use feature name as the default
		String storyName = retrieveStoryName(scenario.getTags());
		if (storyName.isEmpty()) {
			storyName = featureName;
		}
		getScenarioLabels().add(ResultsUtils.createStoryLabel(storyName));

		// FIXED: get parent suite name from tag. If not exist, don't add it
		String parentSuiteName = retrieveParentSuiteName(scenario.getTags());
		if (!parentSuiteName.isEmpty()) {
			getScenarioLabels().add(ResultsUtils.createParentSuiteLabel(parentSuiteName));
		}

		// FIXED: get suite name from tag. If not exist, don't add it
		String suiteName = retrieveSuiteName(scenario.getTags());
		if (!suiteName.isEmpty()) {
			getScenarioLabels().add(ResultsUtils.createSuiteLabel(suiteName));
		}

		// FIXED: get sub suite name from tag. If not exist, use feature name as the default
		String subSuiteName = retrieveSubSuiteName(scenario.getTags());
		if (subSuiteName.isEmpty()) {
			subSuiteName = featureName;
		}
		getScenarioLabels().add(ResultsUtils.createSubSuiteLabel(subSuiteName));

		featurePackage(uri.toString(), featureName)
			.map(ResultsUtils::createPackageLabel)
			.ifPresent(getScenarioLabels()::add);
	}

	private String retrieveSuiteName(List<String> tags) {
		String str = "";
		for (int i = tags.size() - 1; i >= 0; i--) {
			String tag = tags.get(i);
			if (tag.startsWith(LABEL_SUITE)) {
				str = tag.substring(LABEL_SUITE.length() + 1);
				str = str.replace('_', ' ');
				break;
			}
		}
		return str.trim();
	}

	private String retrieveSubSuiteName(List<String> tags) {
		String str = "";
		goOut: for (int i = tags.size() - 1; i >= 0; i--) {
			String tag = tags.get(i);
			for (String label : LABELS_SUB_SUITE) {
				if (tag.startsWith(label)) {
					str = tag.substring(label.length() + 1);
					str = str.replace('_', ' ');
					break goOut;
				}
			}
		}
		return str.trim();
	}

	private String retrieveParentSuiteName(List<String> tags) {
		String str = "";
		goOut: for (int i = tags.size() - 1; i >= 0; i--) {
			String tag = tags.get(i);
			for (String label : LABELS_PARENT_SUITE) {
				if (tag.startsWith(label)) {
					str = tag.substring(label.length() + 1);
					str = str.replace('_', ' ');
					break goOut;
				}
			}
		}
		return str.trim();
	}

	private String retrieveEpicLabel(List<String> tags) {
		String str = "";
		for (int i = tags.size() - 1; i >= 0; i--) {
			String tag = tags.get(i);
			if (tag.startsWith(LABEL_EPIC)) {
				str = tag.substring(LABEL_EPIC.length() + 1);
				str = str.replace('_', ' ');
				break;
			}
		}
		return str.trim();
	}

	private String retrieveStoryName(List<String> tags) {
		String str = "";
		for (int i = tags.size() - 1; i >= 0; i--) {
			String tag = tags.get(i);
			if (tag.startsWith(LABEL_STORY)) {
				str = tag.substring(LABEL_STORY.length() + 1);
				str = str.replace('_', ' ');
				break;
			}
		}
		return str.trim();
	}

	private String retrieveAllureFeatureName(List<String> tags) {
		String str = "";
		for (int i = tags.size() - 1; i >= 0; i--) {
			String tag = tags.get(i);
			if (tag.startsWith(LABEL_FEATURE)) {
				str = tag.substring(LABEL_FEATURE.length() + 1);
				str = str.replace('_', ' ');
				break;
			}
		}
		return str.trim();
	}

	public List<Label> getScenarioLabels() {
		return scenarioLabels;
	}

	public List<Link> getScenarioLinks() {
		return scenarioLinks;
	}

	private Label getTagLabel(final String tag) {
		return ResultsUtils.createTagLabel(tag.substring(1));
	}

	/**
	 * Handle composite named links.
	 *
	 * @param tagString Full tag name and value
	 */
	private void tryHandleNamedLink(final String tagString) {
		final String namedLinkPatternString = PLAIN_LINK + "\\.(\\w+-?)+=(\\w+(-|_)?)+";
		final Pattern namedLinkPattern = Pattern.compile(namedLinkPatternString, Pattern.CASE_INSENSITIVE);

		if (namedLinkPattern.matcher(tagString).matches()) {
			final String type = tagString.split(COMPOSITE_TAG_DELIMITER)[0].split("[.]")[1];
			final String name = tagString.split(COMPOSITE_TAG_DELIMITER)[1];
			getScenarioLinks().add(ResultsUtils.createLink(null, name, null, type));
		} else {
			LOGGER.warn("Composite named tag {} does not match regex {}. Skipping", tagString,
				namedLinkPatternString);
		}
	}

	private Optional<String> featurePackage(final String uriString, final String featureName) {
		final Optional<URI> maybeUri = safeUri(uriString);
		if (!maybeUri.isPresent()) {
			return Optional.empty();
		}
		URI uri = maybeUri.get();

		if (!uri.isOpaque()) {
			final URI work = new File("").toURI();
			uri = work.relativize(uri);
		}
		final String schemeSpecificPart = uri.normalize().getSchemeSpecificPart();
		final Stream<String> folders = Stream.of(schemeSpecificPart.replaceAll("\\.", "_").split("/"));
		final Stream<String> name = Stream.of(featureName);
		return Optional.of(Stream.concat(folders, name)
			.filter(Objects::nonNull)
			.filter(s -> !s.isEmpty())
			.collect(Collectors.joining(".")));
	}

	private static Optional<URI> safeUri(final String uri) {
		try {
			return Optional.of(URI.create(uri));
		} catch (Exception e) {
			LOGGER.debug("could not parse feature uri {}", uri, e);
		}
		return Optional.empty();
	}
}
