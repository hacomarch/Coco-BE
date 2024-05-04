package coco.ide.ideapp.files;

import coco.ide.ideapp.folders.Folder;
import coco.ide.ideapp.projects.Project;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
//Todo : 여기도 프로젝트 필드 추가해야 할 듯. 했으니 확인 필요
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "file_id")
    private Long fileId;

    @NotNull
    @Column(length = 50)
    private String name;

    @NotNull
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @Builder
    public File(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public void changeName(String name) {
        this.name = name;
    }

    //최상위 파일은 프로젝트 필드에 넣어준다.
    public void setProject(Project project) {
        this.project = project;
        if (!project.getFiles().contains(this)) {
            project.getFiles().add(this);
        }
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
        if (folder != null && !folder.getFiles().contains(this)) {
            folder.getFiles().add(this);
        }
    }
}
