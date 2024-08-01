package io.github.semanticpie.pietunes.metadata.core.models.dtos;

import io.github.semanticpie.pietunes.metadata.core.models.enums.ActionType;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ActionEventDto {

    private ActionType type;

    private String entityUuid;

}
