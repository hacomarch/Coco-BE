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
    public File(String name, String path, Project project) {
        this.name = name;
        this.path = path;
        this.project = project;
        if (!project.getFiles().contains(this)) {
            project.getFiles().add(this);
        }
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changePath(String path) {
        this.path = path;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
        if (folder != null && !folder.getFiles().contains(this)) {
            folder.getFiles().add(this);
        }
    }
}
