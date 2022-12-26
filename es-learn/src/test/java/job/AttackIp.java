package job;

import lombok.Data;

import java.util.List;

/**
 * @author hwenj
 * @since 2022/12/26
 */
@Data
public class AttackIp {

    private String first_datetime;

    private String country;

    private List<Integer> info_id_array;
    private Integer last_timestamp;
    private Integer first_timestamp;
    private Long ipl;
    private String city;
    private String last_datetime;
    private Integer expire_timestamp;
    private String ip;
    private Integer ip_type;
    private Long ipl_v6;
    private String province;
    private Integer status;
    private Integer down_timestamp;
    private String down_datetime;
    private String expire_datetime;
    private Integer block_duration;
}
