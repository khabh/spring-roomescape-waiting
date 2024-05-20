package roomescape.web.controller.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import roomescape.service.WaitingService;
import roomescape.service.request.WaitingAppRequest;
import roomescape.service.response.WaitingAppResponse;
import roomescape.web.auth.Auth;
import roomescape.web.controller.request.LoginMember;
import roomescape.web.controller.request.MemberWaitingWebRequest;
import roomescape.web.controller.response.WaitingWebResponse;
import roomescape.web.controller.response.WaitingWithRankWebResponse;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/waitings")
public class MemberWaitingController {

    private final WaitingService waitingService;

    public MemberWaitingController(WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @PostMapping
    public ResponseEntity<WaitingWebResponse> save(@Valid @RequestBody MemberWaitingWebRequest request, @Valid @Auth LoginMember loginMember) {

        WaitingAppResponse waitingAppResponse = waitingService.save(
                new WaitingAppRequest(request.date(), request.timeId(),
                        request.themeId(), loginMember.id()));
        WaitingWebResponse waitingWebResponse = new WaitingWebResponse(waitingAppResponse);

        return ResponseEntity.created(URI.create("/waitings/" + waitingWebResponse.id()))
                .body(waitingWebResponse);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<WaitingWithRankWebResponse>> findMyWaitingWithRank(@Valid @Auth LoginMember loginMember) {
        Long memberId = loginMember.id();
        List<WaitingWithRankWebResponse> waitingWithRankWebResponses = waitingService.findWaitingWithRankByMemberId(memberId)
                .stream()
                .map(WaitingWithRankWebResponse::new)
                .toList();

        return ResponseEntity.ok(waitingWithRankWebResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@Valid @Auth LoginMember loginMember, @PathVariable Long id) {
        waitingService.deleteMemberWaiting(loginMember.id(), id);

        return ResponseEntity.noContent().build();
    }
}
