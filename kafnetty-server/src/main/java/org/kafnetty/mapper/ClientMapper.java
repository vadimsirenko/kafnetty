package org.kafnetty.mapper;

import org.kafnetty.dto.channel.ChannelClientDto;
import org.kafnetty.dto.kafka.KafkaClientDto;
import org.kafnetty.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    @Mapping(ignore = true, target = "messageType")
    @Mapping(ignore = true, target = "operationType")
    ChannelClientDto ClientToChannelClientDto(Client client);

    @Mapping(ignore = true, target = "sent")
    Client ChannelClientDtoToClient(ChannelClientDto clientDto);

    @Mapping(ignore = true, target = "kafkaMessageId")
    @Mapping(ignore = true, target = "clusterId")
    KafkaClientDto ChannelClientDtoToKafkaMessageDto(ChannelClientDto channelClientDto);

    ChannelClientDto KafkaClientDtoToChannelClientDto(KafkaClientDto kafkaClientDto);
}

