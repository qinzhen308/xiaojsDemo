package cn.xiaojs.xma.model.socket.room.whiteboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;

/**
 * Created by Paul Z on 2017/9/11.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BoardLayerSet {

    public ArrayList<SyncLayer> layers;
    public ArrayList<SyncLayer> groupLayers;

}
