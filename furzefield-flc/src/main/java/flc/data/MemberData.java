package flc.data;

import flc.model.Member;

import java.util.List;

public final class MemberData {
    private MemberData() {
    }

    public static List<Member> buildMembers() {
        return List.of(
                new Member("M001", "Member 1"),
                new Member("M002", "Member 2"),
                new Member("M003", "Member 3"),
                new Member("M004", "Member 4"),
                new Member("M005", "Member 5"),
                new Member("M006", "Member 6"),
                new Member("M007", "Member 7"),
                new Member("M008", "Member 8"),
                new Member("M009", "Member 9"),
                new Member("M010", "Member 10")
        );
    }
}
