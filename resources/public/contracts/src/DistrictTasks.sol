pragma solidity ^0.4.24;

import "./Ownable.sol";

/*
    Only Owner of contract can add and update Tasks.
    Everybody can add Bids to Tasks.
    Everybody can Vote once on Bid.
*/

contract DistrictTasks is Ownable {
    struct Bid {
        address creator;
        address[] voters;
        mapping(address => bool) voted;
    }

    struct Task {
        uint biddingEndsOn;
        bool isActive;
        Bid[] bids;
    }

    Task[] public tasks;


    event LogAddTask (
        uint indexed id,
        string title,
        bool isActive,
        uint biddingEndsOn
    );
    
    event LogUpdateTaskActive(uint indexed id, bool isActive);
    event LogUpdateTaskBiddingEndsOn(uint indexed id, uint biddingEndsOn);
    event LogUpdateTaskTitle(uint indexed id, string title);

    event LogAddBid (
        uint indexed taskId,
        uint indexed bidId,
        string titile,
        string description,
        address indexed creator
    );

    event LogUpdateBidTitle(uint taskId, uint bidTitle, string title);
    event LogUpdateBidDescription(uint taskId, uint bidTitle, string description);

    event LogAddVoter (
        uint indexed taskId,
        uint indexed bidId,
        address voter
    );


    modifier notEmptyString(string s) {
        require(bytes(s).length > 0, "String can't be empty");
        _;
    }

    modifier validBiddingEndsOn(uint biddingEndsOn) {
        require(biddingEndsOn > block.timestamp, "biddingEndsOn > now failed, timestamp expired");
        _;
    }

    modifier activeTask(uint taskId) {
        require(tasks[taskId].isActive == true, "Task is set as not active by Onwer of the contract");
        _;
    }

    modifier voterDidntVote(uint taskId, uint bidId) {
        require(tasks[taskId].bids[bidId].voted[msg.sender] != true, "voter already voted");
        _;
    }

    // tasks

    function addTask(string _title, uint _biddingEndsOn, bool _isActive)
    public
    onlyOwner
    notEmptyString(_title)
    validBiddingEndsOn(_biddingEndsOn)
    {
        uint _taskId = tasks.length++;
        tasks[_taskId].biddingEndsOn = _biddingEndsOn;
        tasks[_taskId].isActive = _isActive;
        emit LogAddTask(
            _taskId,
            _title,
            _isActive,
            _biddingEndsOn
        );
    }

    function updateTaskActive(uint _taskId, bool _isActive) public onlyOwner {
        tasks[_taskId].isActive = _isActive;
        emit LogUpdateTaskActive(_taskId, _isActive);
    }

    function updateTaskBiddingEndsOn(uint _taskId, uint _biddingEndsOn)
    public
    onlyOwner
    validBiddingEndsOn(_biddingEndsOn)
    {
        tasks[_taskId].biddingEndsOn = _biddingEndsOn;
        emit LogUpdateTaskBiddingEndsOn(_taskId, _biddingEndsOn);
    }

    function updateTaskTitle(uint _taskId, string _title)
    public
    notEmptyString(_title)
    {
        emit LogUpdateTaskTitle(_taskId, _title);
    }

    function countTasks() public view returns (uint){
        return tasks.length;
    }

    // bids

    function addBid(uint _taskId, string _title, string _description)
    public
    activeTask(_taskId)
    validBiddingEndsOn(tasks[_taskId].biddingEndsOn)
    notEmptyString(_title)
    notEmptyString(_description)
    {
        uint _bidId = tasks[_taskId].bids.length++;
        tasks[_taskId].bids[_bidId].creator = msg.sender;
        emit LogAddBid(_taskId, _bidId, _title, _description, msg.sender);
    }

    function updateBidTitle(uint _taskId, uint _bidId, string _title)
    public
    onlyOwner
    notEmptyString(_title) {
        emit LogUpdateBidTitle(_taskId, _bidId, _title);
    }

    function updateBidDescription(uint _taskId, uint _bidId, string _description)
    public
    onlyOwner
    notEmptyString(_description) {
        emit LogUpdateBidDescription(_taskId, _bidId, _description);
    }

    function countBids(uint _taskId) public view returns (uint){
        return tasks[_taskId].bids.length;
    }

    function getBid(uint _taskId, uint _bidId) public view returns (address) {
        return tasks[_taskId].bids[_bidId].creator;
    }

    // voters

    function addVoter(uint _taskId, uint _bidId)
    public
    activeTask(_taskId)
    validBiddingEndsOn(tasks[_taskId].biddingEndsOn)
    voterDidntVote(_taskId, _bidId)
    {
        tasks[_taskId].bids[_bidId].voters.push(msg.sender);
        tasks[_taskId].bids[_bidId].voted[msg.sender] = true;
        emit LogAddVoter(_taskId, _bidId, msg.sender);
    }

    function getVoters(uint _taskId, uint _bidId) public view returns (address[]) {
        return tasks[_taskId].bids[_bidId].voters;
    }

    function countVoters(uint _taskId, uint _bidId) public view returns (uint){
        return tasks[_taskId].bids[_bidId].voters.length;
    }

    function isVoted(uint _taskId, uint _bidId, address _voter) public view returns (bool) {
        return (tasks[_taskId].bids[_bidId].voted[_voter] == true);
    }
}
