package middle_point_search.backend.common.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

	// 엑셀 파일을 파싱하여 데이터 리스트로 반환하는 메서드
	public static List<Map<String, String>> parseExcelFile(InputStream is) throws Exception {
		List<Map<String, String>> rows = new ArrayList<>();
		try (Workbook workbook = new XSSFWorkbook(is)) {
			Sheet sheet = workbook.getSheetAt(0);
			Row headerRow = sheet.getRow(0);
			List<String> headers = new ArrayList<>();

			// 헤더 추출
			for (Cell cell : headerRow) {
				headers.add(cell.getStringCellValue());
			}

			// 각 행의 데이터를 읽어 Map으로 저장
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				Map<String, String> rowData = new HashMap<>();
				for (int j = 0; j < headers.size(); j++) {
					Cell cell = row.getCell(j);
					rowData.put(headers.get(j), getCellValueAsString(cell));
				}
				rows.add(rowData);
			}
		}
		return rows;
	}

	private static String getCellValueAsString(Cell cell) {
		if (cell == null) return "";
		switch (cell.getCellType()) {
			case STRING:
				return cell.getStringCellValue();
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					return cell.getDateCellValue().toString();
				} else {
					return String.valueOf(cell.getNumericCellValue()); // 소수점 보존
				}
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case FORMULA:
				return cell.getCellFormula();
			default:
				return "";
		}
	}
}