package cn.dx.dao;

import java.util.List;
import java.util.Map;

public interface RoomBillDao {

	List<Map<String, Object>> findRoomBillList();

	void saveRoomBill(Object[] params);

	Map<String, Object> findRoomBillById(Long id);

	void deleteRoomBillById(Long id);

}
