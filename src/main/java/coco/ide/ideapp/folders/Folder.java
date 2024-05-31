package coco.ide.ideapp.folders;

import coco.ide.ideapp.files.File;
import coco.ide.ideapp.projects.Project;
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
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "folder_id")
    private Long folderId;

    @NotNull
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Folder parentFolder;

    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL)
    private List<Folder> childFolders = new ArrayList<>();

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private List<File> files = new ArrayList<>();

    @Builder
    public Folder(String name, Folder parentFolder, List<Folder> childFolders, List<File> files) {
        this.name = name;
        this.parentFolder = parentFolder;
        this.childFolders = childFolders;
        this.files = files;
    }

    public void setProject(Project project) {
        this.project = project;
        if (!project.getFolders().contains(this)) {
            project.getFolders().add(this);
        }
    }

    public void changeName(String newName) {
        if (!this.name.equals(newName)) {
            this.name = newName;
        }
    }

    public void changeParentFolder(Folder parentFolder) {
        if (this.parentFolder != null) {
            this.parentFolder.getChildFolders().remove(this);
        }
        this.parentFolder = parentFolder;
        if (parentFolder != null) {
            parentFolder.getChildFolders().add(this);
        }
    }
}
