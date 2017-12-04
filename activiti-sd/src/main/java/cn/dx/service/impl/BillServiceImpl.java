package cn.dx.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.dx.dao.BillDao;
import cn.dx.service.BillService;

@Service
public class BillServiceImpl implements BillService {

	@Autowired
	private BillDao billDao;
	
	@Override
	public Map<String, Object> findBillById(String billName, Long id) {
		return billDao.findBillById(billName,id);
	}

	@Override
	public void addBill(String username,Map<String, String[]> map) {
		Map<String,Object> params = new HashMap<>();
		Set<Entry<String, String[]>> entrySet = map.entrySet();
		for (Entry<String, String[]> entry : entrySet) {
			params.put(entry.getKey(), entry.getValue()[0]);
		}
		params.put("ProcessStatus", "初始录入");
		params.put("loginname", username);
		String billName = (String)params.get("billName");
		params.remove("billName");
		billDao.addBill(billName,params);
	}

	@Override
	public List<Map<String, Object>> findBillListByUser(String username) {
		return billDao.findBillListByUser(username);
	}

	@Override
	public void deleteBillByIdAndBillName(String id, String billName) {
		billDao.deleteBillByIdAndBillName(id,billName);

	}

	@Override
	public Map<String, Object> findBillByIdAndBillName(String id, String billName) {
		return billDao.findBillByIdAndBillName(id,billName);
	}

	@Override
	public void updateBill(Map<String, String[]> map) {
		Map<String,Object> params = new HashMap<>();
		Set<Entry<String, String[]>> entrySet = map.entrySet();
		for (Entry<String, String[]> entry : entrySet) {
			params.put(entry.getKey(), entry.getValue()[0]);
		}
		String billName = ((String)params.get("BillName")).replace("\"", "");
		params.remove("BillName");
		billDao.update(billName,params);
	}
	
	@Override
	public void updateBillByAudit(Map<String, Object> map) {
		Map<String,Object> params = new HashMap<>();
		Set<Entry<String, Object>> entrySet = map.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			 Object value = entry.getValue();
			 if(value != null && !"".equals(value)){
				 params.put(entry.getKey(), entry.getValue());
			 }
		}
		String billName = ((String)params.get("BillName")).replace("\"", "");
		params.remove("BillName");
		billDao.update(billName,params);
	}

}
