import axios from "axios";

async function signIn({ setSignInText, url, enqueueSnackbar, APIParameters }) {
  try {
    setSignInText?.("Signing in...");
    const data = await axios.get(url);
    if (!data?.data?.[APIParameters.ACCESS_TOKEN]) {
      enqueueSnackbar?.("Error: Please, sign in again", { variant: "error" });
      setSignInText?.("Sign in");
    } else {
      enqueueSnackbar?.("Sucessfully signed in!", { variant: "success" });
      setSignInText?.("Re-Sign in");
    }
  } catch (e) {
    setSignInText?.("Sign in");
    enqueueSnackbar?.(e.message, { variant: "error" });
  }
}
export { signIn };
