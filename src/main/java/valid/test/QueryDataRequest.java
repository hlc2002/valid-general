package valid.test;

import valid.annotion.SizeRange;
import valid.annotion.ValueRange;

import java.util.List;

/**
 * @author spring
 * @since 2024/9/2 15:09:03
 * @apiNote
 * @version 1.0
 */
public class QueryDataRequest {
    @SizeRange(min = 1)
    private List<Integer> statusList;
    @ValueRange(min = 1)
    private Long id;

    public QueryDataRequest() {

    }

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}