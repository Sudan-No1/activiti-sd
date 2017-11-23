package cn.dx.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.dx.dao.RoomBillDao;
import cn.dx.service.RoomBillService;

@Service
public class RoomBillServiceImpl implements RoomBillService {

	@Autowired
	private RoomBillDao roomBillDao;

	@Override
	public List<Map<String, Object>> findRoomBillList() {

		return roomBillDao.findRoomBillList();
	}

	@Override
	public void saveRoomBill(Object[] params) {
		roomBillDao.saveRoomBill(params);
	}

	@Override
	public Map<String, Object> findRoomBillById(Long id) {
		return roomBillDao.findRoomBillById(id);
	}

	@Override
	public void deleteRoomBillById(Long id) {
		roomBillDao.deleteRoomBillById(id);
	}

}
