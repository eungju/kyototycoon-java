package kyototycoon.tsvrpc;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferIndexFinder;
import org.jboss.netty.buffer.ChannelBuffers;

public class TsvEncoding {
    public final String contentType;
    public final ValueEncoding valueEncoding;

    public TsvEncoding(String contentType, ValueEncoding valueEncoding) {
        this.contentType = contentType;
        this.valueEncoding = valueEncoding;
    }

    static final byte[] COLUMN_SEPARATOR = new byte[] { '\t' };
    static final byte[] ROW_SEPARATOR = new byte[] { '\r', '\n' };
    public ChannelBuffer encode(Values input) {
        try {
            ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
            for (KeyValuePair each : input) {
                buffer.writeBytes(valueEncoding.encode(each.key));
                buffer.writeBytes(COLUMN_SEPARATOR);
                buffer.writeBytes(valueEncoding.encode(each.value));
                buffer.writeBytes(ROW_SEPARATOR);
            }
            return buffer;
        } catch (Exception e) {
            throw new RuntimeException("Error while encoding " + input, e);
        }
    }

    static final ChannelBufferIndexFinder COLUMN_SEPARATOR_FINDER = new ChannelBufferIndexFinder() {
        public boolean find(ChannelBuffer buffer, int guessedIndex) {
            byte b = buffer.getByte(guessedIndex);
            return b == '\t' || b == '\r' || b == '\n';
        }
    };
    static final ChannelBufferIndexFinder COLUMN_FINDER = new ChannelBufferIndexFinder() {
        public boolean find(ChannelBuffer buffer, int guessedIndex) {
            return buffer.getByte(guessedIndex) != '\t';
        }
    };
    static final ChannelBufferIndexFinder ROW_SEPARATOR_FINDER = new ChannelBufferIndexFinder() {
        public boolean find(ChannelBuffer buffer, int guessedIndex) {
            byte b = buffer.getByte(guessedIndex);
            return b == '\r' || b == '\n';
        }
    };
    static final ChannelBufferIndexFinder ROW_FINDER = new ChannelBufferIndexFinder() {
        public boolean find(ChannelBuffer buffer, int guessedIndex) {
            byte b = buffer.getByte(guessedIndex);
            return !(b == '\r' || b == '\n');
        }
    };

    public Values decode(ChannelBuffer input) {
        try {
            Values result = new Values();
            while (input.readable()) {
                int ksize = input.bytesBefore(COLUMN_SEPARATOR_FINDER);
                ksize = ksize == -1 ? input.readableBytes() : ksize;
                byte[] key = new byte[ksize];
                input.readBytes(key);

                int tsize = input.bytesBefore(COLUMN_FINDER);
                tsize = tsize == -1 ? input.readableBytes() : tsize;
                input.readerIndex(input.readerIndex() + tsize);

                int vsize = input.bytesBefore(ROW_SEPARATOR_FINDER);
                vsize = vsize == -1 ? input.readableBytes() : vsize;
                byte[] value = new byte[vsize];
                input.readBytes(value);

                int lsize = input.bytesBefore(ROW_FINDER);
                lsize = lsize == -1 ? input.readableBytes() : lsize;
                input.readerIndex(input.readerIndex() + lsize);

                result.put(valueEncoding.decode(key), valueEncoding.decode(value));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error while decoding " + input, e);
        }
    }
}
