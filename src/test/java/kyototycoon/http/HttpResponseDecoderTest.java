package kyototycoon.http;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class HttpResponseDecoderTest {
    HttpResponseDecoder dut;
    ChannelBuffer buffer;

    @Before
    public void beforeEach() {
        buffer = ChannelBuffers.dynamicBuffer();
        dut = new HttpResponseDecoder(buffer);
    }

    @Test
    public void statusLineConsistsOfVersionAndCodeAndReason() throws Exception {
        buffer.writeBytes("HTTP/1.1 200 OK\r\n".getBytes());
        StatusLine actual = dut.statusLine();
        assertThat(actual.version, is("HTTP/1.1"));
        assertThat(actual.code, is(200));
        assertThat(actual.reason, is("OK"));
    }

    @Test(expected=UnderflowDecoderException.class)
    public void statusLineRequiresMoreInput() throws Exception {
        buffer.writeBytes("HTTP/1.1".getBytes());
        dut.statusLine();
    }

    @Test
    public void headerConsistsOfNameAndValue() throws Exception {
        buffer.writeBytes("Name:value\r\n".getBytes());
        Header actual = dut.header();
        assertThat(actual.name, is("Name"));
        assertThat(actual.value, is("value"));
    }

    @Test
    public void headerAllowsWhiteSpacesBeforeValue() throws Exception {
        buffer.writeBytes("Name: \tvalue\r\n".getBytes());
        Header actual = dut.header();
        assertThat(actual.name, is("Name"));
        assertThat(actual.value, is("value"));
    }

    @Test public void headersCanContainNoHeaders() throws Exception {
        buffer.writeBytes("\r\n".getBytes());
        Headers actual = dut.headers();
        assertThat(actual.size(), is(0));
    }

    @Test public void headersCanContainOnlyOneHeader() throws Exception {
        buffer.writeBytes("Name:value\r\n\r\n".getBytes());
        Headers actual = dut.headers();
        assertThat(actual.size(), is(1));
        assertThat(actual.getHeader("Name").value, is("value"));
    }

    @Test public void headersCanContainMultipleHeaders() throws Exception {
        buffer.writeBytes("Content-Length:8\r\nContent-Type:text/html\r\n\r\n".getBytes());
        Headers actual = dut.headers();
        assertThat(actual.size(), is(2));
        assertThat(actual.getHeader("Content-Length").value, is("8"));
        assertThat(actual.getHeader("Content-Type").value, is("text/html"));
    }}
