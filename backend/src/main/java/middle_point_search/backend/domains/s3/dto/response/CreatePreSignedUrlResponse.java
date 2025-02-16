package middle_point_search.backend.domains.s3.dto.response;

public record CreatePreSignedUrlResponse(
	String preSignedUrl,
	String path
) {
}
