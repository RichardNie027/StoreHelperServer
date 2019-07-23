package com.tlg.storehelper.service.impl;

import com.tlg.storehelper.dao.second.InventoryDetailMapper;
import com.tlg.storehelper.dao.second.InventoryMapper;
import com.tlg.storehelper.entity.second.Inventory;
import com.tlg.storehelper.entity.second.InventoryDetail;
import com.tlg.storehelper.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private InventoryDetailMapper inventoryDetailMapper;

    @Override
    public Inventory getInventoryById(long id) {
        return inventoryMapper.selectById(id);
    }

    @Override
    public InventoryDetail getInventoryDetailById(long id) {
        return inventoryDetailMapper.selectById(id);
    }

    @Override
    public List<InventoryDetail> getInventoryDetailsByParentId(long pid) {
        return inventoryDetailMapper.selectByParentId(pid);
    }
}
