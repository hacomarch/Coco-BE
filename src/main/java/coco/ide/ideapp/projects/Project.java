package coco.ide.ideapp.projects;

import coco.ide.ideapp.folders.Folder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
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

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
//    private Member member;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Folder> folders = new ArrayList<>();

    public void changeName(String name) {
        this.name = name;
    }
}
