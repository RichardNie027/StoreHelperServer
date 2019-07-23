package com.tlg.storehelper.service;

import com.tlg.storehelper.entity.second.Inventory;
import com.tlg.storehelper.entity.second.InventoryDetail;

import java.util.List;

public interface BusinessService {

    public Inventory getInventoryById(long id);
    public InventoryDetail getInventoryDetailById(long id);
    public List<InventoryDetail> getInventoryDetailsByParentId(long pid);

}
