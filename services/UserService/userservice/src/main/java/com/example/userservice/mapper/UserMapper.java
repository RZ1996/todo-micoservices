package com.example.userservice.mapper;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    UserDTO mapUserToUserDTO(User user);




}
