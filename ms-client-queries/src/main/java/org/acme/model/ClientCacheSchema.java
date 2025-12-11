package org.acme.model;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

/**
 * Esquema de serialización para la clase {@link ClientCache}.
 * Genera automáticamente el esquema ProtoStream necesario para
 * que Infinispan pueda serializar y deserializar los objetos de tipo
 * ClientCache.
 * 
 * @author Felipe Malaver
 * @since 2025-12-09
 * @version 1.0
 */
@AutoProtoSchemaBuilder(includeClasses = ClientCache.class)
public interface ClientCacheSchema extends GeneratedSchema {

}
