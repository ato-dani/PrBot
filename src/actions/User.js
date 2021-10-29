import axios from "axios";
import {
  SubmitResponseKeys,
  APIParameters,
  ErrorResponseKey,
} from "../config/ApiConfig";

const buildQuery = (queryMap) => {
  let query = "?";
  Object.keys(queryMap).forEach((curQueryKey) => {
    query += curQueryKey + "=" + queryMap[curQueryKey] + "&";
  });
  return query.substring(0, query.length - 1);
};
async function signIn({ setSignInText, url, enqueueSnackbar, setAccessToken }) {
  try {
    setSignInText?.("Signing in...");
    const data = await axios.get(url);
    const error = data?.data?.[ErrorResponseKey.ERROR];
    if (error) {
      enqueueSnackbar?.(error, {  variant: "error"  });
      return;
    }
    if (!data?.data?.[APIParameters.ACCESS_TOKEN]) {
      enqueueSnackbar?.("Error: Please, sign in again", { variant: "error" });
      setSignInText?.("Sign in");
    } else {
      enqueueSnackbar?.("Sucessfully signed in!", { variant: "success" });
      setSignInText?.("Re-Sign in");
      setAccessToken?.(data?.data?.[APIParameters.ACCESS_TOKEN]);
    }
  } catch (e) {
    setSignInText?.("Sign in");
    enqueueSnackbar?.(e.message, { variant: "error" });
  }
}
async function submitPost({ url, query, enqueueSnackbar }) {
  try {
    const data = await axios.get(url + buildQuery(query));
    const error = data?.data?.[ErrorResponseKey.ERROR];
    if (error) {
      enqueueSnackbar?.(error, { variant: "error" });
      return;
    }
    const message = data?.data?.[SubmitResponseKeys.MESSAGE];
    enqueueSnackbar?.(message, {
      variant: data?.data?.[SubmitResponseKeys.SUCCESS_STATUS]
        ? "success"
        : "error",
    });
  } catch (e) {
    console.log("error: " + e);
    enqueueSnackbar?.(e.message, { variant: "error" });
  }
}
export { signIn, submitPost };
