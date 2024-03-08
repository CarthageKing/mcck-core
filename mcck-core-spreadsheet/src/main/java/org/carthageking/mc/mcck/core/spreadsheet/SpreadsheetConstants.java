package org.carthageking.mc.mcck.core.spreadsheet;

/*-
 * #%L
 * mcck-core-spreadsheet
 * %%
 * Copyright (C) 2023 - 2024 Michael I. Calderero
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

public final class SpreadsheetConstants {

	public static final String TAG_WORKBOOK = "workbook";
	public static final String TAG_SHEET = "sheet";
	public static final String TAG_CELL = "c";

	public static final String ATTR_DATATYPE = "dataType";
	public static final String ATTR_DATATYPE_FORMULA = "formula";
	public static final String ATTR_DATATYPE_STRING = "string";
	public static final String ATTR_DATATYPE_NUMBER = "number";
	public static final String ATTR_COL = "col";
	public static final String ATTR_ROW = "row";
	public static final String ATTR_CELLREF = "cellRef";
	public static final String ATR_RAWVALUE = "rawValue";
	public static final String ATTR_FINALVALUE = "finalValue";
	public static final String ATTR_FORMULATYPE = "formulaType";
	public static final String ATTR_INDEX = "index";
	public static final String ATTR_NAME = "name";

	private SpreadsheetConstants() {
		// noop
	}
}
