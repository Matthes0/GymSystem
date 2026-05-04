package pl.matthes0.gym.member;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @EntityGraph(attributePaths = {"membershipPlan", "membershipPlan.gym"})
    List<Member> findAll();

    long countByMembershipPlanIdAndStatus(Long membershipPlanId, Status status);
}
