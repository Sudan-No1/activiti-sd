package cn.dx.service;

import java.util.List;
import java.util.Map;

public interface BillService {

	Map<String, Object> findBillById(String billName, Long id);

	void addBill(String username, Map<String, String[]> map);

	List<Map<String, Object>> findBillListByUser(String username);

	void deleteBillByIdAndBillName(String id, String billName);

	Map<String, Object> findBillByIdAndBillName(String id, String billName);

	void updateBill(Map<String, String[]> map);

	void updateBillByAudit(Map<String, Object> params);

}
