import * as React from 'react';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import {APIPath, APIParameters} from '../../config/ApiConfig';
import { useSnackbar } from 'notistack';
import {signIn} from '../../actions/User';

export default function FormDialog() {
  const [open, setOpen] = React.useState(false);
  const { enqueueSnackbar} = useSnackbar();
  const handleClickOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
  };
  const [signInText, setSignInText] = React.useState("Sign in");
  const handleSignIn =  async () => {
    await signIn({setSignInText, url: APIPath.SIGN_IN_REDDIT, APIParameters, enqueueSnackbar});
  }

  return (
    <div>

      <Button variant="contained" onClick={handleClickOpen}>
        Reddit
      </Button>
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>Reddit</DialogTitle>
        <DialogContent>
          <DialogContentText>
            To post your message to Reddit please sign-in to your Reddit account.
          </DialogContentText>
          <Button onClick={() => handleSignIn()} style={{
            backgroundColor:"#FF5700",
            color: "white",
          }}> {signInText} </Button>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Button 
          variant= "contained"
          color="secondary" 
          onClick={handleClose}
          >
            Submit
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}