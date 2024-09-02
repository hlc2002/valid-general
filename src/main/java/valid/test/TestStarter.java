package valid.test;


import valid.support.custom.AbstractCustomValidSupport;

import java.util.Arrays;

/**
 * @author spring
 * @since 2024/9/2 15:10:56
 * @apiNote
 * @version 1.0
 */
public class TestStarter {
    public static void main(String[] args) {

        AbstractCustomValidSupport validSupport = new AbstractCustomValidSupport();
        QueryDataRequest queryDataRequest = new QueryDataRequest();
        queryDataRequest.setId(-1L);
        queryDataRequest.setStatusList(Arrays.asList(1, 2, 3, 4, 5));
        validSupport.valid(queryDataRequest, QueryDataRequest.class);
    }
}
