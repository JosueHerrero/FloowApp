package com.floow.josueherrero.floowapp.persistence;

import com.floow.josueherrero.floowapp.ui.list.items.Path;

import java.util.List;

/**
 * Created by JosuéManuel on 05/07/2017.
 */

public interface PathsRepository {

    void savePaths(List<Path> pathList);

    List<Path> loadPaths();

    boolean exists();

}
