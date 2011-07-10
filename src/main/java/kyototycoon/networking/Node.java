package kyototycoon.networking;

import kyototycoon.tsv.Values;

public interface Node {
    Values call(String procedure, Values input);
}
