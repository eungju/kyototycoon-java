package kyototycoon.ahc;

import kyototycoon.tsvrpc.TsvEncoding;
import kyototycoon.tsvrpc.TsvEncodingHelper;
import kyototycoon.tsvrpc.TsvRpc;
import kyototycoon.tsvrpc.TsvRpcClient;
import kyototycoon.tsvrpc.TsvRpcConnection;
import kyototycoon.tsvrpc.TsvRpcRequest;
import kyototycoon.tsvrpc.TsvRpcResponse;
import kyototycoon.tsvrpc.Values;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;

public class AhcTsvRpcClient implements TsvRpcClient, TsvRpc {
    private DefaultHttpClient httpClient;
    private URI address;
    private long requestTimeout;

    public void setHost(URI address) {
        this.address = address;
    }

    public void setRequestTimeout(long timeout, TimeUnit unit) {
        requestTimeout = unit.toMillis(timeout);
    }

    public void start() {
        httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager());
    }

    public void stop() {
        httpClient.getConnectionManager().shutdown();
    }

    public TsvRpcResponse call(TsvRpcRequest request) {
        HttpPost httpPost = new HttpPost(address.resolve("/rpc/" + request.procedure));
        httpPost.setEntity(toHttpEntity(request.input));
        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            return new TsvRpcResponse(response.getStatusLine().getStatusCode(), fromHttpEntity(responseEntity));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    HttpEntity toHttpEntity(Values rows) {
        TsvEncoding tsvEncoding = TsvEncodingHelper.forEfficiency(rows);
        ChannelBuffer content = tsvEncoding.encode(rows);
        byte[] bytes = new byte[content.readableBytes()];
        content.readBytes(bytes);
        ByteArrayEntity entity = new ByteArrayEntity(bytes);
        entity.setContentType(tsvEncoding.contentType);
        return entity;
    }

    Values fromHttpEntity(HttpEntity entity) throws IOException {
        InputStream is = entity.getContent();
        try {
            ChannelBuffer buffer = ChannelBuffers.buffer((int) entity.getContentLength());
            buffer.writeBytes(is, buffer.writableBytes());
            return TsvEncodingHelper.forContentType(entity.getContentType().getValue()).decode(buffer);
        } finally {
            is.close();
        }
    }

    public TsvRpcConnection getConnection() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
