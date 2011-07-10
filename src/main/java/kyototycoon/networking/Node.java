package kyototycoon.networking;

import kyototycoon.Values;

public interface Node {
    Values call(String procedure, Values input);
}
