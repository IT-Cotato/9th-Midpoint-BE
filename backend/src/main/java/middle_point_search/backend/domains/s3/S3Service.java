package middle_point_search.backend.domains.s3;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import middle_point_search.backend.common.exception.CustomException;
import middle_point_search.backend.common.exception.errorCode.UserErrorCode;
import middle_point_search.backend.common.properties.S3Properties;
import middle_point_search.backend.domains.s3.dto.response.CreatePreSignedUrlResponse;
import middle_point_search.backend.domains.s3.model.PreSignedUrlPrefix;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

	private final AmazonS3 amazonS3Client;
	private final S3Properties s3Properties;

	private final int EXPIRATION_TIME = 1000 * 60 * 2; // 2분

	// PreSignedUrl 생성, 파일명에는 확장자가 포함되어야 함
	public CreatePreSignedUrlResponse createPreSignedUrl(
		PreSignedUrlPrefix prefix,
		String filename
	) {
		if (!prefix.isValidExtension(getExtension(filename))) {
			throw CustomException.from(UserErrorCode.INVALID_FILE_EXTENSION);
		}

		String path = createPath(prefix.getValue(), filename);

		return new CreatePreSignedUrlResponse(getPreSignedUrlForUpload(path), path);
	}

	// 확장자 가져오기 .제외
	private String getExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	// PreSignedUrl 생성
	private String getPreSignedUrlForUpload(String path) {
		GeneratePresignedUrlRequest generatePresignedUrlRequest = createGeneratePreSignedUrlRequest(path);
		URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
		return url.toString();
	}

	// s3를 통해 PreSignedUrl 생성 요청
	private GeneratePresignedUrlRequest createGeneratePreSignedUrlRequest(String path) {
		return new GeneratePresignedUrlRequest(s3Properties.getBucketName(), path)
			.withMethod(HttpMethod.PUT)
			.withExpiration(getPreSignedUrlExpiration());
	}

	// s3 접근 path 생성
	public String getUrl(String path) {
		return amazonS3Client.getUrl(s3Properties.getBucketName(), path).toString();
	}

	// 파일 존재 여부 확인
	public boolean isFileExists(String path) {
		return amazonS3Client.doesObjectExist(s3Properties.getBucketName(), path);
	}

	// 파일 삭제
	public void deleteFile(String path) {
		amazonS3Client.deleteObject(s3Properties.getBucketName(), path);
	}

	// 파일이 저장될 경로 생성
	private String createPath(String prefix, String fileName) {
		return String.format("%s/%s", prefix, UUID.randomUUID() + "-" + fileName);
	}

	// PreSignedUrl 만료 시간 설정
	private Date getPreSignedUrlExpiration() {
		Date expiration = new Date();
		long expTimeMillis = expiration.getTime();
		expTimeMillis += EXPIRATION_TIME;
		expiration.setTime(expTimeMillis);
		return expiration;
	}
}
