package middle_point_search.backend.domains.s3.model;

import java.util.function.Predicate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PreSignedUrlPrefix {

	PROFILE("profile", extension -> extension.equals("png")
		|| extension.equals("jpg")
		|| extension.equals("jpeg"));

	private final String value;
	private final Predicate<String> validator;

	public boolean isValidExtension(String extension) {
		return validator.test(extension);
	}
}