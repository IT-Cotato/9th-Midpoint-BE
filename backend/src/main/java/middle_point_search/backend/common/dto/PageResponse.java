package middle_point_search.backend.common.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageResponse<T> {
	private List<T> content;
	private int currentPage;
	private int totalPages;
	private long totalItems;


	private PageResponse(List<T> content, int currentPage, int totalPages, long totalElements) {
		this.content = content;
		this.currentPage = currentPage;
		this.totalPages = totalPages;
		this.totalItems = totalElements;
	}

	public static <T> PageResponse<T> from(Page<T> page) {
		return new PageResponse<>(
			page.getContent(),
			page.getNumber(),
			page.getTotalPages(),
			page.getTotalElements()
		);
	}
}