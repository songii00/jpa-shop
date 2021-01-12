package jpabook.jpashop.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.controller.MemberForm;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){

        // 엔티티 외부노출 하지 않기
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id, request.getName()); // command 와 쿼리를 분리
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2(){
        List<Member> findMembers = memberService.findMembers();
        if(ObjectUtils.isEmpty(findMembers)){
            return new Result();
        }
        List<MemberDto> collect = findMembers.stream()
                                             .map(member -> new MemberDto(member.getName()))
                                             .collect(Collectors.toList());
        return new Result(collect);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    static class Result<T> {
        private T data;
    }

    @AllArgsConstructor
    @Data
    static class MemberDto {
        private String name;
    }

    @AllArgsConstructor
    @Data
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @AllArgsConstructor
    @Data
    static class UpdateMemberRequest {
        @NotEmpty
        private String name;
    }

    @AllArgsConstructor
    @Data
    static class CreateMemberResponse {
        private Long id;
    }

    @AllArgsConstructor
    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }
}
