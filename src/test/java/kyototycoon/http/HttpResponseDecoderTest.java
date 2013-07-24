package kyototycoon.http;

import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class HttpResponseDecoderTest {
    HttpResponseDecoder dut;

    @Before
    public void beforeEach() {
        dut = new HttpResponseDecoder(ChannelBuffers.dynamicBuffer());
    }

    @Test
    public void statusLineConsistsOfVersionAndCodeAndReason() throws Exception {
        dut.fill("HTTP/1.1 200 OK\r\n".getBytes());
        StatusLine actual = dut.statusLine();
        assertThat(actual.version, is("HTTP/1.1"));
        assertThat(actual.code, is(200));
        assertThat(actual.reason, is("OK"));
    }

    @Test(expected=UnderflowDecoderException.class)
    public void statusLineRequiresMoreInput() throws Exception {
        dut.fill("HTTP/1.1".getBytes());
        dut.statusLine();
    }

    @Test
    public void headerConsistsOfNameAndValue() throws Exception {
        dut.fill("Name:value\r\n".getBytes());
        Header actual = dut.header();
        assertThat(actual.name, is("Name"));
        assertThat(actual.value, is("value"));
    }

    @Test
    public void headerAllowsWhiteSpacesBeforeValue() throws Exception {
        dut.fill("Name: \tvalue\r\n".getBytes());
        Header actual = dut.header();
        assertThat(actual.name, is("Name"));
        assertThat(actual.value, is("value"));
    }

    @Test public void headersCanContainNoHeaders() throws Exception {
        dut.fill("\r\n".getBytes());
        Headers actual = dut.headers();
        assertThat(actual.size(), is(0));
    }

    @Test public void headersCanContainOnlyOneHeader() throws Exception {
        dut.fill("Name:value\r\n\r\n".getBytes());
        Headers actual = dut.headers();
        assertThat(actual.size(), is(1));
        assertThat(actual.getHeader("Name").value, is("value"));
    }

    @Test public void headersCanContainMultipleHeaders() throws Exception {
        dut.fill("Content-Length:8\r\nContent-Type:text/html\r\n\r\n".getBytes());
        Headers actual = dut.headers();
        assertThat(actual.size(), is(2));
        assertThat(actual.getHeader("Content-Length").value, is("8"));
        assertThat(actual.getHeader("Content-Type").value, is("text/html"));
    }

    @Test public void responseConsistsOfStatusLineAndHeadersAndBody() {
        dut.fill("HTTP/1.1 200 OK\r\n".getBytes());
        assertThat(dut.decode(), nullValue());
        dut.fill("Content-Length: 14\r\n\r\n".getBytes());
        assertThat(dut.decode(), nullValue());
        dut.fill("Hello".getBytes());
        assertThat(dut.decode(), nullValue());
        dut.fill(", World\r\n".getBytes());
        HttpResponse actual = dut.decode();
        assertThat(actual.statusLine.code, is(200));
        assertThat(actual.headers.getHeader("Content-Length").getValueAsInt(), is(14));
        assertThat(actual.body.compareTo(ChannelBuffers.wrappedBuffer("Hello, World\r\n".getBytes())), is(0));
    }
}


