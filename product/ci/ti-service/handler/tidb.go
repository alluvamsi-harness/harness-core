package handler

import (
	"encoding/json"
	"net/http"
	"time"

	"github.com/wings-software/portal/product/ci/ti-service/config"
	"github.com/wings-software/portal/product/ci/ti-service/tidb"
	"go.uber.org/zap"
)

// HandleSelect returns an http.HandlerFunc that figures out which tests to run
// based on the files provided.
func HandleSelect(tidb tidb.TiDB, config config.Config, log *zap.SugaredLogger) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		st := time.Now()
		ctx := r.Context()

		// TODO: Use this information while retrieving from TIDB
		err := validate(r, accountIDParam, repoParam, shaParam, branchParam)
		if err != nil {
			WriteInternalError(w, err)
			return
		}
		accountId := r.FormValue(accountIDParam)
		repo := r.FormValue(repoParam)
		branch := r.FormValue(branchParam)
		sha := r.FormValue(shaParam)

		var files []string
		if err := json.NewDecoder(r.Body).Decode(&files); err != nil {
			WriteBadRequest(w, err)
			log.Errorw("api: could not unmarshal input for test selection",
				"account_id", accountId, "repo", repo, "branch", branch, "sha", sha, zap.Error(err))
			return
		}
		log.Infow("got a files list", "account_id", accountId, "files", files, "repo", repo, "branch", branch, "sha", sha)

		tests, err := tidb.GetTestsToRun(ctx, files)
		if err != nil {
			WriteInternalError(w, err)
			log.Errorw("api: could not select tests", "account_id", accountId,
				"repo", repo, "branch", branch, "sha", sha, zap.Error(err))
			return
		}

		WriteJSON(w, tests, 200)
		log.Infow("completed test selection", "account_id", accountId,
			"repo", repo, "branch", branch, "sha", sha, "tests", tests, "num_tests", len(tests), "time_taken", time.Since(st))

	}
}
