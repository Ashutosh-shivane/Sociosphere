package com.socio.sociosphere.agora.media;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
