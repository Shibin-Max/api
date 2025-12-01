package net.tbu.spi.strategy.channel.dto.gamezone;

import lombok.Data;

import java.util.List;

@Data
public class GAMEZONEResultData {

    private Integer current_page;
    private Integer total_page;
    private Integer page_size;
    private Integer total_number;
    private List<GAMEZONELobbyOrder> rows;
}
