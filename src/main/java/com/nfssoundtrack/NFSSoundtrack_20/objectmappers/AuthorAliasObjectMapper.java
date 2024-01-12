package com.nfssoundtrack.NFSSoundtrack_20.objectmappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.AuthorAliasSerializer;

public class AuthorAliasObjectMapper extends ObjectMapper {

	public AuthorAliasObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(AuthorAlias.class, new AuthorAliasSerializer());
		objectMapper.registerModule(simpleModule);
	}
}
