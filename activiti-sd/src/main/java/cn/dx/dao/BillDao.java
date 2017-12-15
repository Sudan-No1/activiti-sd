package cn.dx.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface BillDao {

	Map<String, Object> findBillById(String billName, Long id);

	void updateBillState(String billName, Map<String, Object> bill);

	void addBill(String billName, Map<String, Object> params);

	List<Map<String, Object>> findBillListByUser(String username);

	void deleteBillByIdAndBillName(String id, String billName);

	Map<String, Object> findBillByIdAndBillName(String id, String billName);

	void update(String billName, Map<String, Object> params);

	Long findBillIdByDescription(String billName, String description);

	int getTotalBillListByUser(String username);

	List<Map<String, Object>> findPageBillListByUser(String username, int startIndex, Integer pageSize);

}
