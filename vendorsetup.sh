rm -rf hardware/qcom-caf/sm8450/audio/graphservices/*

cd hardware/qcom-caf/sm8450/audio/agm
git checkout 62ac0643c907e9566ed99929d947127d8e3b123e
cd ../pal
git checkout 4dfc6be2ac56b7d4aa5b2d919823e612ce1c711b
git revert f3af214667071cd42394f05708633b0ae75eb819
cd ../../../../..
