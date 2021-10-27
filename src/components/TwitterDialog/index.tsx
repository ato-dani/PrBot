import * as React from 'react';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';



export default function TwitterForm() {
  const [open, setOpen] = React.useState(false);

  const handleClickOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
  };
  const [email, setEmail] = React.useState("");
  const [password, setPassword] = React.useState("");

  return (
    <div>
      <Button variant="contained" onClick={handleClickOpen}>
        Twitter
      </Button>
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>Twitter</DialogTitle>
        <DialogContent>
          <DialogContentText>
            To post your message to Twitter please sign-in to your Twitter account.
          </DialogContentText>
          <TextField
            autoFocus
            margin="dense"
            value={email}
            id="email"
            label="Email Address"
            type="email"
            onChange={e => setEmail(e.target.value)}
            fullWidth
            variant="standard"
          />
            <TextField
            autoFocus
            margin="dense"
            value={password}
            id="password"
            label="Password"
            type="password"
            onChange={e => setPassword(e.target.value)}
            fullWidth
            variant="standard"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Button 
          variant= "contained"
          color="primary" 
          type="submit"
          onClick={handleClose}
          >
            Submit
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}
