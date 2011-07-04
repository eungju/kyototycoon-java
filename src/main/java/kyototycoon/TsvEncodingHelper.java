package kyototycoon;

public class TsvEncodingHelper {
    private static final TsvEncoding RAW_TSV_ENCODING = new TsvEncoding("text/tab-separated-values", new RawValueEncoding());
    private static final TsvEncoding URL_TSV_ENCODING = new TsvEncoding("text/tab-separated-values; colenc=U", new UrlValueEncoding());
    private static final TsvEncoding BASE64_TSV_ENCODING = new TsvEncoding("text/tab-separated-values; colenc=B", new Base64ValueEncoding());
    private static final TsvEncoding[] SUPPORTED = new TsvEncoding[] {
            RAW_TSV_ENCODING,
            URL_TSV_ENCODING,
            BASE64_TSV_ENCODING
    };

    public static TsvEncoding forContentType(String contentType) {
        for (TsvEncoding each : SUPPORTED) {
            if (each.contentType.equals(contentType)) {
                return each;
            }
        }
        throw new IllegalArgumentException("Unknown content type " + contentType);
    }

    public static TsvEncoding forEfficiency(Values input) {
        return BASE64_TSV_ENCODING;
    }
}
