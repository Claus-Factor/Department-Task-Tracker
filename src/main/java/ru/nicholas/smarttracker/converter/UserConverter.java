package ru.nicholas.smarttracker.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.nicholas.smarttracker.model.dto.SignUpRequest;
import ru.nicholas.smarttracker.model.dto.UserDto;
import ru.nicholas.smarttracker.model.enity.User;

@Mapper(componentModel = "spring")
public interface UserConverter {
    User toEntity(SignUpRequest signUpRequest);

    @Mapping(target = "departmentName", expression = "java((user.getDepartment()!= null)?user.getDepartment().getName():\"\")")
    UserDto toDto(User user);

    default Page<UserDto> toDtoPage(Page<User> users) {
        return users.map(this::toDto);
    }
}
