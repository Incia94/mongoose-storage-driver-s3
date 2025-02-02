package com.emc.mongoose.storage.driver.coop.netty.http.s3;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.AsciiString;
import io.netty.util.AttributeKey;

import java.nio.charset.StandardCharsets;

/**
Created by kurila on 02.08.16.
*/
public interface S3Api {

	String PREFIX_KEY_X_AMZ = "x-amz-";

	String AUTH_PREFIX = "AWS ";

	String AUTH_V4_PREFIX = "AWS4-HMAC-SHA256 ";

	String KEY_X_AMZ_COPY_SOURCE = PREFIX_KEY_X_AMZ + "copy-source";

	String KEY_X_AMZ_SECURITY_TOKEN = PREFIX_KEY_X_AMZ + "security-token";

	AsciiString HEADERS_CANONICAL[] = {
			HttpHeaderNames.CONTENT_MD5,
			HttpHeaderNames.CONTENT_TYPE,
			//HttpHeaderNames.RANGE,
			HttpHeaderNames.DATE
	};

	AsciiString HEADERS_CANONICAL_V4[] = {
			HttpHeaderNames.CONTENT_MD5,
			HttpHeaderNames.CONTENT_TYPE,
			//HttpHeaderNames.RANGE,
			HttpHeaderNames.DATE,
			HttpHeaderNames.HOST,
	};
	String URL_ARG_VERSIONING = "versioning";

	String SIGN_METHOD = "HmacSHA1";

	String SIGN_V4_METHOD = "HmacSHA256";

	byte[] VERSIONING_ENABLE_CONTENT = ("<VersioningConfiguration xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\">" +
					"<Status>Enabled</Status></VersioningConfiguration>").getBytes(StandardCharsets.US_ASCII);

	byte[] VERSIONING_DISABLE_CONTENT = ("<VersioningConfiguration xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\">" +
					"<Status>Suspended</Status></VersioningConfiguration>").getBytes(StandardCharsets.US_ASCII);

	String KEY_UPLOAD_ID = "uploadId";

	AttributeKey<String> KEY_ATTR_UPLOAD_ID = AttributeKey.newInstance(KEY_UPLOAD_ID);

	String COMPLETE_MPU_HEADER = "<CompleteMultipartUpload>\n";
	String COMPLETE_MPU_PART_NUM_START = "\t<Part>\n\t\t<PartNumber>";
	String COMPLETE_MPU_PART_NUM_END = "</PartNumber>\n\t\t<ETag>";
	String COMPLETE_MPU_PART_ETAG_END = "</ETag>\n\t</Part>\n";
	String COMPLETE_MPU_FOOTER = "</CompleteMultipartUpload>";

	String TAGGING_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<Tagging xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\">\n\t<TagSet>\n";
	String TAGGING_ENTRY_START = "\t\t<Tag><Key>";
	String TAGGING_ENTRY_MIDDLE = "</Key><Value>";
	String TAGGING_ENTRY_END = "</Value></Tag>\n";
	String TAGGING_FOOTER = "\t</TagSet>\n</Tagging>\n";

	int MAX_KEYS_LIMIT = 1000;
	String QNAME_ITEM = "Contents";
	String QNAME_ITEM_ID = "Key";
	String QNAME_ITEM_SIZE = "Size";
	String QNAME_IS_TRUNCATED = "IsTruncated";
}
