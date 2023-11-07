package org.kafnetty.dto.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MESSAGE_TYPE;
import org.kafnetty.type.OPERATION_TYPE;

import java.util.UUID;

@Getter
@Setter
public class ChannelClientDto extends ChannelBaseDto {
    private UUID id;
    private String login;
    private String email;
    private String nickName;
    private String token;
    private UUID roomId;
    private String clusterId;

    public ChannelClientDto() {
        super(MESSAGE_TYPE.CLIENT, OPERATION_TYPE.NONE);
    }

    @Override
    public ChannelFuture writeAndFlush(Channel channel) {
        this.token = null;
        return super.writeAndFlush(channel);
    }
}
