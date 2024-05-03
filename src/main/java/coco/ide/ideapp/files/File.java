package coco.ide.ideapp.files;

import coco.ide.ideapp.folders.Folder;
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

    public void setFolder(Folder folder) {
        this.folder = folder;
        if (folder != null && !folder.getFiles().contains(this)) {
            folder.getFiles().add(this);
        }
    }
}
