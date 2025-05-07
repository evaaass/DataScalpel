package cn.superhuang.data.scalpel.lib.jdbc.model.jdbc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JdbcQueryDataOrder {
	private String column;
	/**
	 * desc (倒序) | asc (正序)
	 */
	private JdbcQueryDataOrderDirection direction;
}
