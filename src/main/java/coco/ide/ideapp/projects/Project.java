package coco.ide.ideapp.projects;

import coco.ide.ideapp.files.File;
import coco.ide.ideapp.folders.Folder;
import coco.ide.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "project_id")
    private Long projectId;

    @NotNull
    @Column(length = 50)
    private String name;

    @NotNull
    private String language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Folder> folders = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<File> files = new ArrayList<>();

    @Builder
    public Project(String name, String language, List<Folder> folders, List<File> files) {
        this.name = name;
        this.language = language;
        this.folders = folders;
        this.files = files;
    }

    public void setMember(Member member) {
        this.member = member;
        if (!member.getProjects().contains(this)) {
            member.getProjects().add(this);
        }
    }

    public void changeName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("프로젝트 명은 빈 칸일 수 없습니다.");
        }
        if (!this.name.equals(newName)) {
            this.name = newName;
        }
    }
}
