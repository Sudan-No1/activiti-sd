package cn.dx.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public interface RoomBillService {
	List<Map<String,Object>> findRoomBillList();

	void saveRoomBill(Object[] params);

	Map<String,Object> findRoomBillById(Long id);

	void deleteRoomBillById(Long id);
}
