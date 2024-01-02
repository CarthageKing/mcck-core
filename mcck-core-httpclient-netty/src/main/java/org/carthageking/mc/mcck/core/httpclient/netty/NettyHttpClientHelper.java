package org.carthageking.mc.mcck.core.httpclient.netty;

/*-
 * #%L
 * mcck-core-httpclient-netty
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

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import org.apache.http.HttpHeaders;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelper;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperException;
import org.carthageking.mc.mcck.core.httpclient.HttpClientHelperResult;
import org.carthageking.mc.mcck.core.httpclient.HttpMimeTypes;
import org.carthageking.mc.mcck.core.httpclient.StatusLine;
import org.carthageking.mc.mcck.core.jse.McckIOUtil;
import org.carthageking.mc.mcck.core.jse.McckUriUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslContext;

public class NettyHttpClientHelper implements HttpClientHelper, Closeable, AutoCloseable {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NettyHttpClientHelper.class);

	private final Bootstrap bootstrap;
	private final SslContext sslContext;
	private final int numRoutesPerHostAndPort;

	private volatile boolean closing;
	private EventLoopGroup eventLoopGroup;
	private ConcurrentMap<HostAndPort, ChannelList> channelMap = new ConcurrentHashMap<>();
	private ConcurrentMap<ChannelId, HttpChannelInfo> channelIdMap = new ConcurrentHashMap<>();

	public NettyHttpClientHelper(Bootstrap bootstrap) {
		this(bootstrap, 1, 1, null);
	}

	public NettyHttpClientHelper(Bootstrap bootstrap, int numThreads, int numRoutesPerHostAndPort, SslContext sslContext) {
		this.bootstrap = bootstrap;
		this.numRoutesPerHostAndPort = numRoutesPerHostAndPort;
		this.sslContext = sslContext;
		eventLoopGroup = new NioEventLoopGroup(numThreads);
		bootstrap.group(eventLoopGroup)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.SO_KEEPALIVE, true)
			.option(ChannelOption.SO_REUSEADDR, true)
			.handler(new HttpChannelIniter(this.sslContext));
	}

	@Override
	public void close() throws IOException {
		boolean cls = closing;
		closing = true;
		if (cls) {
			return;
		}
		try {
			closeChannels();
			eventLoopGroup.shutdownGracefully().get();
		} catch (Exception e) {
			log.warn("Error while closing " + this + ":\n", e);
		}
		eventLoopGroup = null;
	}

	private void closeChannels() {
		// TODO: do we need this?
	}

	@Override
	protected void finalize() throws Throwable {
		McckIOUtil.closeFully(this);
		super.finalize();
	}

	@Override
	public HttpClientHelperResult<String> doGet(URI requestUri, HttpHeadersModifierHelper httpHdrModHelper) {
		int dstPort = McckUriUtil.getPort(requestUri);
		Channel channel = retrieveChannel(requestUri, dstPort);
		HttpMethod httpMethod = HttpMethod.GET;
		return doRequestResponse(requestUri, httpMethod, channel, null, 0, httpHdrModHelper);
	}

	@Override
	public HttpClientHelperResult<String> doDelete(URI requestUri, HttpHeadersModifierHelper httpHdrModHelper) {
		int dstPort = McckUriUtil.getPort(requestUri);
		Channel channel = retrieveChannel(requestUri, dstPort);
		HttpMethod httpMethod = HttpMethod.DELETE;
		return doRequestResponse(requestUri, httpMethod, channel, null, 0, httpHdrModHelper);
	}

	@Override
	public HttpClientHelperResult<String> doPost(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper) {
		int dstPort = McckUriUtil.getPort(requestUri);
		Channel channel = retrieveChannel(requestUri, dstPort);
		HttpMethod httpMethod = HttpMethod.POST;
		ByteBuf content = Unpooled.wrappedBuffer(body.getBytes(StandardCharsets.UTF_8));
		return doRequestResponse(requestUri, httpMethod, channel, content, body.length(), httpHdrModHelper);
	}

	@Override
	public HttpClientHelperResult<String> doPut(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper) {
		int dstPort = McckUriUtil.getPort(requestUri);
		Channel channel = retrieveChannel(requestUri, dstPort);
		HttpMethod httpMethod = HttpMethod.PUT;
		ByteBuf content = Unpooled.wrappedBuffer(body.getBytes(StandardCharsets.UTF_8));
		return doRequestResponse(requestUri, httpMethod, channel, content, body.length(), httpHdrModHelper);
	}

	@Override
	public HttpClientHelperResult<String> doPatch(URI requestUri, String body, HttpHeadersModifierHelper httpHdrModHelper) {
		int dstPort = McckUriUtil.getPort(requestUri);
		Channel channel = retrieveChannel(requestUri, dstPort);
		HttpMethod httpMethod = HttpMethod.PATCH;
		ByteBuf content = Unpooled.wrappedBuffer(body.getBytes(StandardCharsets.UTF_8));
		return doRequestResponse(requestUri, httpMethod, channel, content, body.length(), httpHdrModHelper);
	}

	private Channel retrieveChannel(URI requestUri, int port) {
		if (closing) {
			throw new IllegalStateException("This object " + this + " is in the process of being closed!");
		}
		HostAndPort hap = new HostAndPort(requestUri.getHost(), port);
		ChannelList clst = channelMap.computeIfAbsent(hap, k -> new ChannelList());
		Channel[] channel = { null };
		doSynchronize(clst.getLock(), l -> {
			while (null == channel[0]) {
				for (Channel c : clst.getChannels()) {
					if (c.isActive()) {
						HttpChannelInfo hci = channelIdMap.get(c.id());
						if (hci.getState().canAcceptRequests()) {
							channel[0] = c;
							hci.reset();
							hci.setState(ChannelInfoState.TAKEN);
							break;
						}
					}
				}
				if (null == channel[0] && clst.getChannels().size() < numRoutesPerHostAndPort) {
					channel[0] = bootstrap.connect(hap.getHost(), hap.getPort()).awaitUninterruptibly().channel();
					clst.getChannels().add(channel[0]);

					HttpChannelInfo hci = new HttpChannelInfo(channel[0].id(), clst);
					channelIdMap.put(hci.getChannelId(), hci);
				}
				if (null == channel[0]) {
					try {
						clst.getLockCondition().await();
					} catch (InterruptedException e) {
						throw new HttpClientHelperException(e);
					}
				}
			}
		});
		return channel[0];
	}

	private HttpClientHelperResult<String> doRequestResponse(URI requestUri,
		HttpMethod httpMethod, Channel channel, ByteBuf content, int contentLength,
		HttpHeadersModifierHelper httpHdrModHelper) {
		ByteBuf theContent = content;
		boolean haveContent = true;
		if (null == theContent) {
			haveContent = false;
			contentLength = 0;
			theContent = Unpooled.EMPTY_BUFFER;
		}
		String rawPath = requestUri.getRawPath();
		HttpRequest httpReq = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, httpMethod, rawPath, theContent);
		HttpHeadersModifier hdrMod = createHttpHeaderModifier(httpReq);

		// needed headers
		hdrMod.setHeader(HttpHeaders.HOST, requestUri.getHost());
		hdrMod.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));

		if (null != httpHdrModHelper) {
			httpHdrModHelper.modify(hdrMod);
		} else {
			if (haveContent) {
				hdrMod.setHeader(HttpHeaders.CONTENT_TYPE, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
			}
			hdrMod.setHeader(HttpHeaders.ACCEPT, HttpMimeTypes.APPLICATION_JSON_UTF8.getMimeTypeAsString());
		}
		HttpChannelInfo hci = channelIdMap.get(channel.id());
		channel.writeAndFlush(httpReq);

		doSynchronize(hci.getLock(), l -> {
			while (!ChannelInfoState.RECEIVED.equals(hci.getState())) {
				try {
					hci.getLockCondition().await();
				} catch (InterruptedException e) {
					throw new HttpClientHelperException(e);
				}
			}
		});

		HttpResponse httpRsp = hci.getHttpResponse();
		StatusLine status = new StatusLine(httpRsp.status().code(), httpRsp.status().reasonPhrase());
		String rspBody = createStringRequestBody(hci);
		HttpClientHelperResult<String> result = new HttpClientHelperResult<>(status, toMap(httpRsp.headers()), rspBody);
		doSynchronize(hci.getChannelList().getLock(), l -> {
			hci.setState(ChannelInfoState.DONE);
			hci.getChannelList().getLockCondition().signalAll();
		});
		return result;
	}

	private String createStringRequestBody(HttpChannelInfo hci) {
		String str = hci.getByteArrayOS().toString(StandardCharsets.UTF_8);
		hci.getByteArrayOS().reset();
		return str;
	}

	private Map<String, List<String>> toMap(io.netty.handler.codec.http.HttpHeaders headers) {
		Map<String, List<String>> map = new HashMap<>();
		for (Entry<String, String> ent : headers) {
			List<String> lst = map.computeIfAbsent(ent.getKey(), k -> new ArrayList<>());
			lst.add(ent.getValue());
		}
		return map;
	}

	private void doSynchronize(ReentrantLock lock, Consumer<ReentrantLock> consumer) {
		try {
			lock.lock();
			consumer.accept(lock);
		} finally {
			lock.unlock();
		}
	}

	private HttpHeadersModifier createHttpHeaderModifier(HttpRequest httpReq) {
		return new HttpHeadersModifier() {

			@Override
			public void setHeader(String name, String value) {
				httpReq.headers().set(name, value);
			}

			@Override
			public void addHeader(String name, String value) {
				httpReq.headers().add(name, value);
			}
		};
	}

	private class HttpChannelIniter extends ChannelInitializer<SocketChannel> {

		private final SslContext innerSslCtx;

		public HttpChannelIniter(SslContext innerSslCtx) {
			this.innerSslCtx = innerSslCtx;
		}

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ChannelPipeline cp = ch.pipeline();

			if (null != innerSslCtx) {
				cp.addLast(innerSslCtx.newHandler(ch.alloc()));
			}

			cp.addLast(new HttpClientCodec());
			cp.addLast(new HttpContentDecompressor());
			cp.addLast(new HttpResponseChannelHandler());
		}
	}

	private class HttpResponseChannelHandler extends SimpleChannelInboundHandler<HttpObject> {

		public HttpResponseChannelHandler() {
			// noop
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
			HttpChannelInfo hci = channelIdMap.get(ctx.channel().id());
			//log.trace("Got a HTTP object: {}", msg);
			if (msg instanceof HttpResponse) {
				hci.setHttpResponse((HttpResponse) msg);
			} else if (msg instanceof HttpContent) {
				HttpContent content = (HttpContent) msg;
				//log.trace("Got content: {}", content.content().toString(StandardCharsets.UTF_8));
				content.content().forEachByte(b -> {
					hci.getByteArrayOS().write(b);
					return true;
				});
				if (msg instanceof LastHttpContent) {
					doSynchronize(hci.getLock(), l -> {
						hci.setState(ChannelInfoState.RECEIVED);
						hci.getLockCondition().signal();
					});
				}
			} else {
				throw new IllegalArgumentException("Unsupported object type " + msg.getClass());
			}
		}
	}

	private enum ChannelInfoState {
		READY(true), TAKEN(false), RECEIVED(false), DONE(true);

		private boolean canAcceptRequests;

		private ChannelInfoState(boolean canAcceptRequests) {
			this.canAcceptRequests = canAcceptRequests;
		}

		public boolean canAcceptRequests() {
			return canAcceptRequests;
		}
	}

	private static class ChannelList {
		private final List<Channel> channels = new ArrayList<>();
		private final ReentrantLock lock;
		private final Condition lockCondition;

		public ChannelList() {
			this(true);
		}

		public ChannelList(boolean fairLock) {
			lock = new ReentrantLock(fairLock);
			lockCondition = lock.newCondition();
		}

		public List<Channel> getChannels() {
			return channels;
		}

		public ReentrantLock getLock() {
			return lock;
		}

		public Condition getLockCondition() {
			return lockCondition;
		}
	}

	private static class HttpChannelInfo {
		private volatile ChannelInfoState state = ChannelInfoState.READY;
		private final ChannelId channelId;
		private final ChannelList channelList;
		private final ReentrantLock lock;
		private final Condition lockCondition;
		private HttpResponse httpResponse;
		private ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream(1024);

		public HttpChannelInfo(ChannelId channelId, ChannelList channelList) {
			this(channelId, channelList, true);
		}

		public HttpChannelInfo(ChannelId channelId, ChannelList channelList, boolean fairLock) {
			this.channelId = channelId;
			this.channelList = channelList;
			lock = new ReentrantLock(fairLock);
			lockCondition = lock.newCondition();
		}

		public void reset() {
			httpResponse = null;
			byteArrayOS.reset();
			state = ChannelInfoState.READY;
		}

		public ChannelInfoState getState() {
			return state;
		}

		public void setState(ChannelInfoState state) {
			this.state = state;
		}

		public HttpResponse getHttpResponse() {
			return httpResponse;
		}

		public void setHttpResponse(HttpResponse httpResponse) {
			this.httpResponse = httpResponse;
		}

		public ByteArrayOutputStream getByteArrayOS() {
			return byteArrayOS;
		}

		public ChannelId getChannelId() {
			return channelId;
		}

		public ChannelList getChannelList() {
			return channelList;
		}

		public ReentrantLock getLock() {
			return lock;
		}

		public Condition getLockCondition() {
			return lockCondition;
		}
	}
}
