package com.inventory.handler;

import org.springframework.beans.factory.annotation.Autowired;

public class InventoryHandlerFactory {

    @Autowired
    private DefaultInventoryHandler defaultHandler; // injected bean


    //similarly other handlers
//    @Autowired
//    private FifoInventoryHandler fifoHandler;
//    @Autowired
//    private LifoInventoryHandler lifoHandler;
//    @Autowired
//    private ReservedInventoryHandler reservedHandler;
    public InventoryHandler getHandler(String policy)
    {
        if (policy == null || policy.isEmpty())
            return defaultHandler;
//        switch(policy.toLowerCase())
//        {
//            case "fifo": return fifoHandler;
//            case "lifo": return lifoHandler;
//            case "reserved": return reservedHandler;
//            default: return defaultHandler;
//        }
        return defaultHandler;
    }

}
