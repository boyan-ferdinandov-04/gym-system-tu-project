package com.example.gym_management.dto;

import com.example.gym_management.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    public static MemberDTO fromEntity(Member member) {
        if (member == null) {
            return null;
        }
        return new MemberDTO(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail()
        );
    }
}
