package cn.dx.service;

import java.util.Map;

import cn.dx.domain.PageBean;

public interface BillService {

	Map<String, Object> findBillById(String billName, Long id);

	void addBill(String username, Map<String, String[]> map);

	PageBean<Map<String, Object>> findBillListByUser(String username, Integer pageNum, Integer pageSize);

	void deleteBillByIdAndBillName(String id, String billName);

	Map<String, Object> findBillByIdAndBillName(String id, String billName);

	void updateBill(Map<String, String[]> map);

	void updateBillByAudit(Map<String, Object> params);

	Long findBillIdByDescription(String billName, String description);

}
